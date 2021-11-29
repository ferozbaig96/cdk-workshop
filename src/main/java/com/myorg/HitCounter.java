package com.myorg;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;

import java.util.HashMap;
import java.util.Map;

public class HitCounter extends Construct {

	private final Function handler;
	private final Table table;

	public HitCounter(final Construct scope, final String id, final HitCounterProps props) {
		super(scope, id);

		this.table = Table.Builder.create(this, "Hits")
			.partitionKey(Attribute.builder()
				.name("path")
				.type(AttributeType.STRING)
				.build())
			.build();

		final Map<String, String> environment = new HashMap<>();
		environment.put("DOWNSTREAM_FUNCTION_NAME", props.getDownstream().getFunctionName());
		environment.put("HITS_TABLE_NAME", this.table.getTableName());

		this.handler = Function.Builder.create(this, "HitCounterHandler")
			.runtime(Runtime.NODEJS_14_X)
			.handler("hitcounter.handler")
			.code(Code.fromAsset("src/lambda"))
			.environment(environment)
			.build();

		// Grants the lambda function read/write permissions to our table
		this.table.grantReadWriteData(this.handler);

		// Grants the lambda function invoke permissions to the downstream function
		props.getDownstream().grantInvoke(this.handler);
	}

	public Function getHandler() {
		return handler;
	}

	public Table getTable() {
		return table;
	}
}
