package com.craigdietrich.covid19indigenous.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class AnswerVo {
    @SerializedName("action")
    @Expose
    var action: String? = null

    @SerializedName("questionnaire_id")
    @Expose
    var questionnaireId: String? = null

    @SerializedName("key")
    @Expose
    var key: String? = null

    @SerializedName("created")
    @Expose
    var created: String? = null

    @SerializedName("answers")
    @Expose
    var answers: List<Answer>? = null

    class Answer {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("answers")
        @Expose
        var answers: List<String>? = null

        @SerializedName("created")
        @Expose
        var created: String? = null
    }
}

