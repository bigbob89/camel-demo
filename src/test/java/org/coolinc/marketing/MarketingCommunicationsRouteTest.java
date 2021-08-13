package org.coolinc.marketing;

import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MarketingCommunicationsRouteTest extends CamelTestSupport {

    @Test
    public void messagesForMarketingChannelsAreSent() throws Exception {
        // given
        AdviceWith.adviceWith(context, "marketing-route", a -> {
            a.replaceFromWith("direct:start");
            a.interceptSendToEndpoint("kafka:social-media-topic?brokers=localhost:29092")
                    .skipSendToOriginalEndpoint().to("mock:kafka-1");
            a.interceptSendToEndpoint("kafka:local-papers-topic?brokers=localhost:29092")
                    .skipSendToOriginalEndpoint().to("mock:kafka-2");
            a.interceptSendToEndpoint("kafka:error-topic?brokers=localhost:29092")
                    .skipSendToOriginalEndpoint().to("mock:kafka-3");
        });
        String audience = "marketing channels";
        String body = String.format("{\n" +
                "  \"message\": \"Interested in world domination? Cool Inc. is looking to hire someone like you!\",\n" +
                "  \"audience\": \"%s\"\n" +
                "}", audience);

        MockEndpoint resultEndpoint1 = context.getEndpoint("mock:kafka-1", MockEndpoint.class);
        resultEndpoint1.expectedMessageCount(1);
        resultEndpoint1.expectedBodiesReceived(body);

        MockEndpoint resultEndpoint2 = context.getEndpoint("mock:kafka-2", MockEndpoint.class);
        resultEndpoint2.expectedMessageCount(1);
        resultEndpoint2.expectedBodiesReceived(body);

        MockEndpoint resultEndpoint3 = context.getEndpoint("mock:kafka-3", MockEndpoint.class);
        resultEndpoint3.expectedMessageCount(0);

        // when
        template.sendBody("direct:start", body);

        // then
        assertMockEndpointsSatisfied();
    }

    @Test
    @Disabled("todo")
    public void messagesForOtherChannelsAreFilteredOut() throws InterruptedException {
        // given
        String audience = "Cool Inc. minions";
        String body = String.format("{\n" +
                "  \"message\": \"Interested in world domination? Cool Inc. is looking to hire someone like you!\",\n" +
                "  \"comments\": \"This came in from HR, please post it in your channel\",\n" +
                "  \"audience\": \"%s\"\n" +
                "}", audience);

        MockEndpoint resultEndpoint1 = resolveMandatoryEndpoint("mock:topic-1", MockEndpoint.class);
        resultEndpoint1.expectedMessageCount(0);

        MockEndpoint resultEndpoint2 = resolveMandatoryEndpoint("mock:topic-2", MockEndpoint.class);
        resultEndpoint2.expectedMessageCount(0);

        MockEndpoint resultEndpoint3 = resolveMandatoryEndpoint("mock:error", MockEndpoint.class);
        resultEndpoint3.expectedMessageCount(0);

        // when
        template.sendBody("direct:start", body);

        // then
        resultEndpoint1.assertIsSatisfied();
        resultEndpoint2.assertIsSatisfied();
        resultEndpoint3.assertIsSatisfied();
    }

    @Test
    @Disabled("todo")
    public void invalidMessagesAreSentToErrorTopic() throws InterruptedException {
        String body = "no json";

        MockEndpoint resultEndpoint1 = resolveMandatoryEndpoint("mock:topic-1", MockEndpoint.class);
        resultEndpoint1.expectedMessageCount(0);

        MockEndpoint resultEndpoint2 = resolveMandatoryEndpoint("mock:topic-2", MockEndpoint.class);
        resultEndpoint2.expectedMessageCount(0);

        MockEndpoint resultEndpoint3 = resolveMandatoryEndpoint("mock:error", MockEndpoint.class);
        resultEndpoint3.expectedMessageCount(1);

        template.sendBody("direct:start", body);

        resultEndpoint1.assertIsSatisfied();
        resultEndpoint2.assertIsSatisfied();
        resultEndpoint3.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new MarketingCommunicationsRoute();
    }

}
