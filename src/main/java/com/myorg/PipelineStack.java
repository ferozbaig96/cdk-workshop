package com.myorg;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;

public class PipelineStack extends Stack {
	public PipelineStack(final Construct parent, final String id) {
		this(parent, id, null);
	}

	public PipelineStack(final Construct parent, final String id, final StackProps props) {
		super(parent, id, props);

		// Pipeline code goes here
	}
}
