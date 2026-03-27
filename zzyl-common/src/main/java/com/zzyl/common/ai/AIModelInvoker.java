package com.zzyl.common.ai;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.common.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class AIModelInvoker {

    @Autowired
    private BailianAIProperties bailianAIProperties;

    public String bailianInvoker(String prompt) {
        try {
            Generation gen = new Generation();

            Message userMsg = Message.builder()
                    .role("user")
                    .content( prompt )
                    .build();
            GenerationParam param = GenerationParam.builder()
                    // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                    .apiKey(bailianAIProperties.getKey())
                    .model(bailianAIProperties.getModel())
                    .messages(Arrays.asList( userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .enableThinking(true)
                    .build();
            return gen.call(param).getOutput().getChoices().get(0).getMessage().getContent();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}