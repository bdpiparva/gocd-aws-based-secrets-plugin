package com.thoughtworks.gocd.secretmanager.aws;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.github.bdpiparva.plugin.base.executors.secrets.LookupExecutor;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.gocd.secretmanager.aws.models.SecretConfig;
import com.thoughtworks.gocd.secretmanager.aws.models.Secrets;
import com.thoughtworks.gocd.secretmanager.aws.request.SecretConfigRequest;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import static com.github.bdpiparva.plugin.base.GsonTransformer.fromJson;
import static com.github.bdpiparva.plugin.base.GsonTransformer.toJson;
import static java.util.Collections.singletonMap;

public class SecretConfigLookupExecutor extends LookupExecutor<SecretConfigRequest> {
    private AWSClientFactory awsClientFactory;

    public SecretConfigLookupExecutor() {
        this(new AWSClientFactory(new AWSCredentialsProviderChain()));
    }

    SecretConfigLookupExecutor(AWSClientFactory awsClientFactory) {
        this.awsClientFactory = awsClientFactory;
    }

    @Override
    protected GoPluginApiResponse execute(SecretConfigRequest request) {
        AWSSecretsManager client = null;
        try {
            SecretConfig requestConfiguration = request.getConfiguration();
            client = awsClientFactory.client(requestConfiguration);

            List<String> secretIds = request.getKeys();
            if (secretIds == null || secretIds.isEmpty()) {
                return DefaultGoPluginApiResponse.badRequest("No secret key provided!!!");
            }
            String secretName = requestConfiguration.getSecretName();
            GetSecretValueResult secretValueResult = client.getSecretValue(new GetSecretValueRequest().withSecretId(secretName));

            String secretStringValue = secretValueResult.getSecretString();
            ByteBuffer secretBinaryValue = secretValueResult.getSecretBinary();

            final Secrets secrets = new Secrets();
            if (!StringUtils.isEmpty(secretStringValue)) {
                Map json = new Gson().fromJson(secretStringValue, Map.class);

                for (String secretId : secretIds) {
                    if (json.containsKey(secretId)) {
                        secrets.add(secretId, json.get(secretId).toString());
                    }
                }

            } else if (secretBinaryValue != null) {
                secrets.add(secretName, secretBinaryValue.toString());
            }
            return DefaultGoPluginApiResponse.success(toJson(secrets));
        } catch (Exception e) {
            LOGGER.error("Failed to lookup secret from AWS.", e);
            return DefaultGoPluginApiResponse.error(toJson(singletonMap("message", "Failed to lookup secrets from AWS. See logs for more information.")));
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    @Override
    protected SecretConfigRequest parseRequest(String body) {
        return fromJson(body, SecretConfigRequest.class);
    }
}
