package com.myorg;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.pipelines.CodeBuildStep;
import software.amazon.awscdk.pipelines.CodePipeline;
import software.amazon.awscdk.pipelines.CodePipelineSource;
import software.amazon.awscdk.pipelines.StageDeployment;
import software.amazon.awscdk.services.codecommit.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PipelineStack extends Stack {
	public PipelineStack(final Construct parent, final String id) {
		this(parent, id, null);
	}

	public PipelineStack(final Construct parent, final String id, final StackProps props) {
		super(parent, id, props);

		// CodeCommit repo 'WorkshopRepo'
		final Repository repo = Repository.Builder.create(this, "WorkshopRepo")
			.repositoryName("WorkshopRepo")
			.build();

		// CodePipeline
		final CodePipeline pipeline = CodePipeline.Builder.create(this, "Pipeline")
			.pipelineName("WorkshopPipeline")
			.synth(CodeBuildStep.Builder.create("SynthStep")
				.input(CodePipelineSource.codeCommit(repo, "master"))
				.installCommands(Arrays.asList(
					"npm install -g aws-cdk"  // Commands to run before build
				))
				.commands(Arrays.asList(
					"mvn package",
					"npx cdk synth"
				))
				.build())
			.build();

		// CodePipeline Stage to deploy application
		final WorkshopPipelineStage deploy = new WorkshopPipelineStage(this, "Deploy");
		StageDeployment stageDeployment = pipeline.addStage(deploy);

		// Tests phase
		Map<String, CfnOutput> hashMap1 = new HashMap<>();
		hashMap1.put("ENDPOINT_URL", deploy.hcViewerUrl);
		Map<String, CfnOutput> hashMap2 = new HashMap<>();
		hashMap1.put("ENDPOINT_URL", deploy.hcEndpoint);

		stageDeployment.addPost(
			CodeBuildStep.Builder.create("TestViewerEndpoint")
				.projectName("TestViewerEndpoint")
				.commands(Arrays.asList("curl -Ssf $ENDPOINT_URL"))
				.envFromCfnOutputs(hashMap1)
				.build(),
			CodeBuildStep.Builder.create("TestAPIGatewayEndpoint")
				.projectName("TestAPIGatewayEndpoint")
				.envFromCfnOutputs(hashMap2)
				.commands(Arrays.asList(
					"curl -Ssf $ENDPOINT_URL",
					"curl -Ssf $ENDPOINT_URL/hello",
					"curl -Ssf $ENDPOINT_URL/test"
				))
				.build()
		);
	}
}
