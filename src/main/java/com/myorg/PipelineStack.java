package com.myorg;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.codecommit.Repository;

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
	}
}
