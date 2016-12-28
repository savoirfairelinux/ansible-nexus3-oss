import groovy.json.JsonSlurper
import org.sonatype.nexus.repository.config.Configuration

parsed_args = new JsonSlurper().parseText(args)

authentication = parsed_args.remote_username == null ? null : [
        type: 'username',
        username: parsed_args.remote_username,
        password: parsed_args.remote_password
]

/*
@param name                        The name of the new Repository
@param remoteUrl                   The url of the external proxy for this Repository
@param indexType                   Use 'REGISTRY' to use the proxy url for the index as well. Use 'HUB' to use the
                                   index from DockerHub. Use 'CUSTOM' in conjunction with the 'indexUrl' param to
                                   specify a custom index location
@param indexUrl                    The url of a 'CUSTOM' index; only used in conjunction with the 'indexType'
                                   parameter
@param httpPort                    The http port to accept traffic for this Repository on (optional)
@param httpsPort                   The https port to accept traffic for this Repository on (optional)
@param blobStoreName               The BlobStore the Repository should use
@param strictContentTypeValidation Whether or not the Repository should enforce strict content types
@param v1Enabled                   Whether or not this Repository supports Docker V1 format
@return the newly created Repository

Repository createDockerProxy(final String name,
                             final String remoteUrl,
                             final String indexType,
                             final String indexUrl,
                             Integer httpPort,
                             Integer httpsPort,
                             final String blobStoreName,
                             final boolean strictContentTypeValidation,
                             final boolean v1Enabled);
*/


/*
    Configuration configuration = createProxy(name, 'docker-proxy', remoteUrl, blobStoreName, strictContentTypeValidation)
    configuration.attributes.docker = configureDockerAttributes(httpPort, httpsPort, v1Enabled)
    configuration.attributes.dockerProxy = [
        indexType: indexType,
        indexUrl : indexUrl
    ]
    configuration.attributes.httpclient.connection.useTrustStore = true
    createRepository(configuration)
 */

configuration = new Configuration(
        repositoryName: parsed_args.name,
        recipeName: 'docker-proxy',
        online: true,
        attributes: [
                docker : [
                        v1Enabled: Boolean.valueOf(parsed_args.v1_enabled),
                ],
                dockerProxy : [
                        indexUrl: parsed_args.index_url,
                        indexType: parsed_args.index_type
                ],

                proxy  : [
                        remoteUrl: parsed_args.remote_url,
                        contentMaxAge: 1440.0,
                        metadataMaxAge: 1440.0,
                ],
                httpclient: [
                        blocked: false,
                        autoBlock: true,
                        authentication: authentication,
                        connection: [
                                useTrustStore: false
                        ]
                ],
                storage: [
                        blobStoreName: parsed_args.blob_store,
                        strictContentTypeValidation: Boolean.valueOf(parsed_args.strict_content_validation)
                ],
                negativeCache: [
                        enabled: true,
                        timeToLive: 1440.0
                ]
        ]
)

def existingRepository = repository.getRepositoryManager().get(parsed_args.name)

if (existingRepository != null) {
    existingRepository.stop()
    configuration.attributes['storage']['blobStoreName'] = existingRepository.configuration.attributes['storage']['blobStoreName']
    existingRepository.update(configuration)
    existingRepository.start()
} else {
    repository.getRepositoryManager().create(configuration)
}
