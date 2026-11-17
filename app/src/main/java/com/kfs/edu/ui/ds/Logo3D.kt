package com.kfs.edu.ui.ds

import android.content.Context
import android.graphics.Bitmap
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

/**
 * Tiny self-contained renderer for the university's 3D logo (a flat-coloured glTF/GLB, no textures).
 * It parses the GLB and rasterises it with a z-buffer + Gouraud shading, so no 3D engine dependency is needed.
 */
class Logo3D private constructor(
    private val pos: List<FloatArray>,
    private val nor: List<FloatArray>,
    private val idx: List<IntArray>,
    private val col: List<IntArray>, // r,g,b per mesh
) {
    private var zbuf = FloatArray(0)
    private var pix = IntArray(0)

    /** Renders the model rotated by [yawDeg] (around Y, positive turns the face to the right) and [pitchDeg]. */
    fun render(bmp: Bitmap, yawDeg: Float, pitchDeg: Float, heightFraction: Float) {
        val w = bmp.width; val h = bmp.height
        if (pix.size != w * h) { pix = IntArray(w * h); zbuf = FloatArray(w * h) }
        java.util.Arrays.fill(pix, 0)
        java.util.Arrays.fill(zbuf, -1e9f)
        val ya = Math.toRadians(yawDeg.toDouble()); val pa = Math.toRadians(pitchDeg.toDouble())
        val cy = cos(ya).toFloat(); val sy = sin(ya).toFloat(); val cp = cos(pa).toFloat(); val sp = sin(pa).toFloat()
        val scale = h * heightFraction / 0.9f
        val cx = w / 2f; val cyy = h / 2f
        // light from top-left-front
        val lx = -0.35f; val ly = 0.5f; val lz = 0.79f

        for (m in pos.indices) {
            val p = pos[m]; val n = nor[m]; val ix = idx[m]
            val vc = p.size / 3
            val sx = FloatArray(vc); val sY = FloatArray(vc); val sz = FloatArray(vc); val it = FloatArray(vc)
            for (v in 0 until vc) {
                val x = p[v * 3]; val y = p[v * 3 + 1]; val z = p[v * 3 + 2]
                val nx = n[v * 3]; val ny = n[v * 3 + 1]; val nz = n[v * 3 + 2]
                // yaw (Y), then pitch (X)
                val x1 = x * cy + z * sy; val z1 = -x * sy + z * cy
                val y2 = y * cp - z1 * sp; val z2 = y * sp + z1 * cp
                val nx1 = nx * cy + nz * sy; val nz1 = -nx * sy + nz * cy
                val ny2 = ny * cp - nz1 * sp; val nz2 = ny * sp + nz1 * cp
                sx[v] = cx + x1 * scale; sY[v] = cyy - y2 * scale; sz[v] = z2 * scale
                it[v] = (0.5f + 0.62f * max(0f, nx1 * lx + ny2 * ly + nz2 * lz)).coerceAtMost(1.08f)
            }
            val r = col[m][0]; val g = col[m][1]; val b = col[m][2]
            var t = 0
            while (t + 2 < ix.size) {
                tri(w, h, sx, sY, sz, it, ix[t], ix[t + 1], ix[t + 2], r, g, b)
                t += 3
            }
        }
        bmp.setPixels(pix, 0, w, 0, 0, w, h)
    }

    private fun tri(w: Int, h: Int, sx: FloatArray, sy: FloatArray, sz: FloatArray, it: FloatArray, a: Int, b: Int, c: Int, r: Int, g: Int, bl: Int) {
        val x0 = sx[a]; val y0 = sy[a]; val x1 = sx[b]; val y1 = sy[b]; val x2 = sx[c]; val y2 = sy[c]
        val area = (x1 - x0) * (y2 - y0) - (x2 - x0) * (y1 - y0)
        if (abs(area) < 1e-6f) return
        val minX = max(0, kotlin.math.floor(min(x0, min(x1, x2)).toDouble()).toInt())
        val maxX = min(w - 1, kotlin.math.ceil(max(x0, max(x1, x2)).toDouble()).toInt())
        val minY = max(0, kotlin.math.floor(min(y0, min(y1, y2)).toDouble()).toInt())
        val maxY = min(h - 1, kotlin.math.ceil(max(y0, max(y1, y2)).toDouble()).toInt())
        val inv = 1f / area
        val z0 = sz[a]; val z1 = sz[b]; val z2 = sz[c]
        val i0 = it[a]; val i1 = it[b]; val i2 = it[c]
        for (py in minY..maxY) {
            val fy = py + 0.5f
            for (px in minX..maxX) {
                val fx = px + 0.5f
                val w0 = ((x1 - fx) * (y2 - fy) - (x2 - fx) * (y1 - fy)) * inv
                val w1 = ((x2 - fx) * (y0 - fy) - (x0 - fx) * (y2 - fy)) * inv
                val w2 = 1f - w0 - w1
                if (w0 < -0.001f || w1 < -0.001f || w2 < -0.001f) continue
                val z = w0 * z0 + w1 * z1 + w2 * z2
                val o = py * w + px
                if (z <= zbuf[o]) continue
                zbuf[o] = z
                val k = w0 * i0 + w1 * i1 + w2 * i2
                val rr = (r * k).toInt().coerceIn(0, 255); val gg = (g * k).toInt().coerceIn(0, 255); val bb = (bl * k).toInt().coerceIn(0, 255)
                pix[o] = (0xFF shl 24) or (rr shl 16) or (gg shl 8) or bb
            }
        }
    }

    companion object {
        fun load(context: Context, asset: String): Logo3D? = try {
            val data = context.assets.open(asset).use { it.readBytes() }
            val bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
            val jsonLen = bb.getInt(12)
            val json = JSONObject(String(data, 20, jsonLen, Charsets.UTF_8))
            val binStart = 20 + jsonLen + 8
            val views = json.getJSONArray("bufferViews"); val accs = json.getJSONArray("accessors")
            fun accessorOffset(i: Int): Pair<Int, JSONObject> {
                val a = accs.getJSONObject(i); val v = views.getJSONObject(a.getInt("bufferView"))
                return binStart + v.optInt("byteOffset", 0) + a.optInt("byteOffset", 0) to a
            }
            fun floats(i: Int): FloatArray { val (o, a) = accessorOffset(i); val n = a.getInt("count") * 3; return FloatArray(n) { bb.getFloat(o + it * 4) } }
            fun ints(i: Int): IntArray {
                val (o, a) = accessorOffset(i); val n = a.getInt("count")
                return if (a.getInt("componentType") == 5125) IntArray(n) { bb.getInt(o + it * 4) } else IntArray(n) { bb.getShort(o + it * 2).toInt() and 0xFFFF }
            }
            val mats = json.getJSONArray("materials")
            fun srgb(f: Double) = (f.coerceIn(0.0, 1.0).pow(1.0 / 2.2) * 255).toInt()
            val pos = ArrayList<FloatArray>(); val nor = ArrayList<FloatArray>(); val idx = ArrayList<IntArray>(); val col = ArrayList<IntArray>()
            val meshes = json.getJSONArray("meshes")
            for (i in 0 until meshes.length()) {
                val p = meshes.getJSONObject(i).getJSONArray("primitives").getJSONObject(0)
                val at = p.getJSONObject("attributes")
                pos.add(floats(at.getInt("POSITION"))); nor.add(floats(at.getInt("NORMAL"))); idx.add(ints(p.getInt("indices")))
                val bc = mats.getJSONObject(p.getInt("material")).getJSONObject("pbrMetallicRoughness").getJSONArray("baseColorFactor")
                col.add(intArrayOf(srgb(bc.getDouble(0)), srgb(bc.getDouble(1)), srgb(bc.getDouble(2))))
            }
            Logo3D(pos, nor, idx, col)
        } catch (_: Exception) { null }
    }
}
