package foundationgames.enhancedblockentities.common.util.mfrapi.indigo.ao;

public class AoFaceData {

    public float a0;
    public float a1;
    public float a2;
    public float a3;

    public int b0;
    public int b1;
    public int b2;
    public int b3;
    public int s0;
    public int s1;
    public int s2;
    public int s3;

    public void l0(int l0) {
        this.b0 = l0 & 0xFFFF;
        this.s0 = (l0 >>> 16) & 0xFFFF;
    }

    public void l1(int l1) {
        this.b1 = l1 & 0xFFFF;
        this.s1 = (l1 >>> 16) & 0xFFFF;
    }

    public void l2(int l2) {
        this.b2 = l2 & 0xFFFF;
        this.s2 = (l2 >>> 16) & 0xFFFF;
    }

    public void l3(int l3) {
        this.b3 = l3 & 0xFFFF;
        this.s3 = (l3 >>> 16) & 0xFFFF;
    }

    public int weigtedBlockLight(float[] w) {
        return (int) (b0 * w[0] + b1 * w[1] + b2 * w[2] + b3 * w[3]) & 0xFF;
    }

    public int weigtedSkyLight(float[] w) {
        return (int) (s0 * w[0] + s1 * w[1] + s2 * w[2] + s3 * w[3]) & 0xFF;
    }

    public int weightedCombinedLight(float[] w) {
        return weigtedSkyLight(w) << 16 | weigtedBlockLight(w);
    }

    public float weigtedAo(float[] w) {
        return a0 * w[0] + a1 * w[1] + a2 * w[2] + a3 * w[3];
    }

    public void toArray(float[] aOut, int[] bOut, int[] vertexMap) {
        aOut[vertexMap[0]] = a0;
        aOut[vertexMap[1]] = a1;
        aOut[vertexMap[2]] = a2;
        aOut[vertexMap[3]] = a3;
        bOut[vertexMap[0]] = s0 << 16 | b0;
        bOut[vertexMap[1]] = s1 << 16 | b1;
        bOut[vertexMap[2]] = s2 << 16 | b2;
        bOut[vertexMap[3]] = s3 << 16 | b3;
    }

    public static AoFaceData weightedMean(AoFaceData in0, float w0, AoFaceData in1, float w1, AoFaceData out) {
        out.a0 = in0.a0 * w0 + in1.a0 * w1;
        out.a1 = in0.a1 * w0 + in1.a1 * w1;
        out.a2 = in0.a2 * w0 + in1.a2 * w1;
        out.a3 = in0.a3 * w0 + in1.a3 * w1;

        out.b0 = (int) (in0.b0 * w0 + in1.b0 * w1);
        out.b1 = (int) (in0.b1 * w0 + in1.b1 * w1);
        out.b2 = (int) (in0.b2 * w0 + in1.b2 * w1);
        out.b3 = (int) (in0.b3 * w0 + in1.b3 * w1);

        out.s0 = (int) (in0.s0 * w0 + in1.s0 * w1);
        out.s1 = (int) (in0.s1 * w0 + in1.s1 * w1);
        out.s2 = (int) (in0.s2 * w0 + in1.s2 * w1);
        out.s3 = (int) (in0.s3 * w0 + in1.s3 * w1);

        return out;
    }
}
