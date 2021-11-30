package com.myorg;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.pipelines.CodeBuildStep;
import software.amazon.awscdk.pipelines.CodePipeline;
import software.amazon.awscdk.pipelines.CodePipelineSource;
import software.amazon.awscdk.services.codecommit.Repository;

import java.util.Arrays;

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
		pipeline.addStage(deploy);
	}
}
