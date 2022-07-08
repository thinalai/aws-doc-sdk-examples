/*
 * Copyright 2010-2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package aws.example.elasticloadbalancingv2;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder;
import com.amazonaws.services.elasticloadbalancingv2.model.ModifyRuleResult;
import com.amazonaws.services.elasticloadbalancingv2.model.RuleCondition;
import com.amazonaws.services.elasticloadbalancingv2.model.TargetGroupStickinessConfig;
import com.amazonaws.services.elasticloadbalancingv2.model.TargetGroupTuple;
import com.amazonaws.services.elasticloadbalancingv2.model.Action;
import com.amazonaws.services.elasticloadbalancingv2.model.ActionTypeEnum;
import com.amazonaws.services.elasticloadbalancingv2.model.ForwardActionConfig;
import com.amazonaws.services.elasticloadbalancingv2.model.HostHeaderConditionConfig;
import com.amazonaws.services.elasticloadbalancingv2.model.ModifyRuleRequest;

/**
 * Replaces the specified properties of the specified rule
 * https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/elasticloadbalancingv2/AmazonElasticLoadBalancing.html#modifyRule-com.amazonaws.services.elasticloadbalancingv2.model.ModifyRuleRequest-
 */
public class ModifyRule
{
    public static void main(String[] args)
    {
        final String USAGE =
            "To run this example, supply a rule_arn and list of group arn with weight\n" +
            "Ex: ModifyRule <rule_arn> <target_group_A_arn=X> <target_group_B_arn=Y> ...\n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String ruleArn = args[0];
        List<TargetGroupTuple> targetGroups = new ArrayList<>();
        for (int i=1; i<args.length; i++) {
            String[] kv = args[i].split("=", 2);
            targetGroups.add(new TargetGroupTuple()
                .withTargetGroupArn(kv[0])
                .withWeight(new Integer(kv[1])));
        }

        final AmazonElasticLoadBalancing elb = AmazonElasticLoadBalancingClientBuilder.defaultClient();

        // Conditions:
        // [
        //     {
        //         "Field": "host-header",
        //         "Values": [
        //             "www.example.com"
        //         ]
        //     }
        // ]
        RuleCondition condition = new RuleCondition()
            .withField("host-header")
            .withHostHeaderConfig(new HostHeaderConditionConfig()
                .withValues("www.example.com"));

        // Actions:
        // [
        //     {
        //         "Type": "forward",
        //         "Order": 1,
        //         "ForwardConfig": {
        //             "TargetGroups": [
        //                 {
        //                     "TargetGroupArn": "arn:aws:elasticloadbalancing:ap-northeast-1:419854513669:targetgroup/tg-internal-alb-a2/74d677f78474871c",
        //                     "Weight": 0
        //                 },
        //                 {
        //                     "TargetGroupArn": "arn:aws:elasticloadbalancing:ap-northeast-1:419854513669:targetgroup/tg-internal-alb-a1/fb1fbbfb453bc738",
        //                     "Weight": 50
        //                 }
        //             ],
        //             "TargetGroupStickinessConfig": {
        //                 "Enabled": true,
        //                 "DurationSeconds": 10800
        //             }
        //         }
        //     }
        // ]
        ForwardActionConfig forwardConfig = new ForwardActionConfig()
            .withTargetGroups(targetGroups) // set list of target group with weight
            .withTargetGroupStickinessConfig(new TargetGroupStickinessConfig() // set stickiness
                .withDurationSeconds(10800)
                .withEnabled(true));
        Action act = new Action()
            .withOrder(1)
            .withType(ActionTypeEnum.Forward)
            .withForwardConfig(forwardConfig);

        // Send modifyRule request
        ModifyRuleRequest request = new ModifyRuleRequest()
            .withRuleArn(ruleArn)
            .withConditions(condition)
            .withActions(act);

        ModifyRuleResult response = elb.modifyRule(request);

        System.out.printf(
            "Successfully modified %s", ruleArn);
    }
}
