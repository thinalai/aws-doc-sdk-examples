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
package aws.example.cloudfront;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder;
import com.amazonaws.services.cloudfront.model.CreateInvalidationRequest;
import com.amazonaws.services.cloudfront.model.CreateInvalidationResult;
import com.amazonaws.services.cloudfront.model.InvalidationBatch;
import com.amazonaws.services.cloudfront.model.Paths;


/**
 * Create a new invalidation
 * https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/cloudfront/AmazonCloudFront.html#createInvalidation-com.amazonaws.services.cloudfront.model.CreateInvalidationRequest-
 */
public class CreateInvalidation
{
    public static void main(String[] args)
    {
        final String USAGE =
            "To run this example, supply a distribution-id and list of paths\n" +
            "Ex: CreateInvalidation <distribution_id> <path_1> <path_2> ...\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String distributionId = args[0];
        List<String> ps = new ArrayList<>();
        for (int i=1; i<args.length; i++) {
            ps.add(args[i]);
        }
        Paths paths = new Paths().withItems(ps).withQuantity(new Integer(ps.size()));

        // callerRef: use to uniquely identify an invalidation request
        String callerRef = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

        final AmazonCloudFront cf = AmazonCloudFrontClientBuilder.defaultClient();
        CreateInvalidationRequest request = new CreateInvalidationRequest(distributionId, new InvalidationBatch(paths, callerRef));
        CreateInvalidationResult response = cf.createInvalidation(request);

        System.out.printf(
            "Successfully created %s", response.getLocation());
    }
}
