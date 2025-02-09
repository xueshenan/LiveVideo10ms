package constantin.video.example.decodingperf

import constantin.video.core.player.DecodingInfo
import constantin.video.example.Helper


class TestResultDecodingInfoConstructor{

    companion object {
        fun create(vs_source: Int,vs_assets_filename: String,decodingInfo : DecodingInfo) : TestResultDecodingInfo {
            val ret = TestResultDecodingInfo(Helper.getDate(), Helper.getTime(), Helper.isEmulator(), vs_source, vs_assets_filename,
                    decodingInfo.currentFPS, decodingInfo.currentKiloBitsPerSecond, decodingInfo.avgParsingTime_ms, decodingInfo.avgWaitForInputBTime_ms,
                    decodingInfo.avgHWDecodingTime_ms, decodingInfo.avgTotalDecodingTime_ms, decodingInfo.nNALU, decodingInfo.nNALUSFeeded);
            return ret;
        }
    }

}