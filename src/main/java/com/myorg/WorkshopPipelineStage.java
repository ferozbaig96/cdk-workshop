package com.myorg;

import software.amazon.awscdk.core.Stage;
import software.amazon.awscdk.core.StageProps;
import software.constructs.Construct;

public class WorkshopPipelineStage extends Stage {
	public WorkshopPipelineStage(final Construct scope, final String id, final StageProps props) {
		super(scope, id, props);

		new CdkWorkshopStack(this, "WebService");
	}

	public WorkshopPipelineStage(final Construct scope, final String id) {
		this(scope, id, null);
	}
}
