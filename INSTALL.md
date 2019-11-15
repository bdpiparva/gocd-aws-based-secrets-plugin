# AWS Secrets Manager plugin for GoCD

The plugin needs to be configured with a secret config in order to connect to AWS Secrets Manager.

Table of Contents
=================

  * [Requirements](#requirements)
  * [Installation](#installation)
  * [Configuration](#configure-the-plugin-to-access-secrets-from-aws)
  * [Using secrets](#using-secrets)
  

## Requirements

* GoCD server version `v19.6.0` or above
* AWS credentials to access secrets from [AWS Secrets Manager](https://aws.amazon.com/secrets-manager/)

## Installation

* Copy the file `build/libs/gocd-aws-based-secrets-plugin-VERSION.jar` to the GoCD server under `${GO_SERVER_DIR}/plugins/external`
and restart the server.
* The `GO_SERVER_DIR` is usually `/var/lib/go-server` on **Linux** and `C:\Program Files\Go Server` on **Windows**.

## Configure the plugin to access secrets from AWS

- Login to your GoCD server.
- Navigate to **Admin** > **Secret Management**.
- Click on **ADD** button.
- Configure the mandatory fields.

    | Field           | Required  | Description                                                         |
    | --------------- | --------- | --------------------------------------------------------------------|
    | Endpoint        | true      | The AWS service endpoint for the plugin to connect.                 |
    | AccessKey       | true      | The access key as a part of AWS credentials.                        |
    | SecretAccessKey | true      | The secret access key as a part of AWS credentials.                 |
    | Region          | true      | Region in which AWS secrets manager is hosted.                      |
    | SecretName      | true      | The name of the secret to be utilized.                              |
    | SecretCacheTTL  | false     | The secrets cache TTL in milliseconds, defaults to 30 minutes.      |

    **NOTE:** *The plugin caches secrets for a duration configured using the SecretCacheTTL. Currently GoCD does not provide a 
way to invalidate the cache. To invalidate the cache, change the SecretCacheTTL and save the SecretConfig.*

- Configure the `rules` where this secrets can be used.
`<rules>` tag defines where this secretConfig is allowed/denied to be referred. For more details about rules and examples refer the GoCD Secret Management [documentation](https://docs.gocd.org/current/configuration/secrets_management.html#step-3-restrict-usage-of-secrets-manager)

- Save.

## Using secrets
- See [Define Secret Params](https://docs.gocd.org/current/configuration/secrets_management.html#step-4-define-secret-params) for more information
