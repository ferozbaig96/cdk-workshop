package com.myorg;

import com.github.eladb.dynamotableviewer.TableViewer;
import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;

public class CdkWorkshopStack extends Stack {

	public final CfnOutput hcViewerUrl;
	public final CfnOutput hcEndpoint;

	public CdkWorkshopStack(final Construct parent, final String id) {
		this(parent, id, null);
	}

	public CdkWorkshopStack(final Construct parent, final String id, final StackProps props) {
		super(parent, id, props);

		// Architecture: API Gateway (REST) -> Lambda (proxy)

		// lambda
		final Function hello = Function.Builder.create(this, "HelloHandler")
			.runtime(Runtime.NODEJS_14_X)
			.code(Code.fromAsset("src/lambda"))
			.handler("hello.handler")
			.build();

		// hitCounter
		final HitCounter helloWithCounter = new HitCounter(this, "HelloHitCounter",
			HitCounterProps.builder()
				.downstream(hello)
				.build());

		// api gateway REST API
		final LambdaRestApi gateway = LambdaRestApi.Builder.create(this, "Endpoint")
			.handler(helloWithCounter.getHandler())
			.build();

		// Viewer for the HitCounts table
		final TableViewer tableViewer = TableViewer.Builder.create(this, "ViewerHitCount")
			.title("Hello Hits")
			.table(helloWithCounter.getTable())
			.build();

		hcViewerUrl = CfnOutput.Builder.create(this, "TableViewUrl")
			.value(tableViewer.getEndpoint())
			.build();

		hcEndpoint = CfnOutput.Builder.create(this, "GatewayUrl")
			.value(gateway.getUrl())
			.build();
	}
}
