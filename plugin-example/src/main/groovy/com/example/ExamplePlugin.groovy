package com.example

import com.example.plugin.Plugin

class ExamplePlugin implements Plugin {

    private TheAnswerProvider answerProvider = new TheAnswerProvider()

    @Override
    String getTitle() {
        return "Some example plugin"
    }

    @Override
    int getTheAnswer() {
        return answerProvider.answer
    }
}
