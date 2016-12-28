import groovy.json.JsonSlurper
import org.sonatype.nexus.repository.config.Configuration

parsed_args = new JsonSlurper().parseText(args)

/*
Create a Docker group repository.
@param name The name of the new Repository
@param httpPort The http port to accept traffic for this Repository on (optional)
@param httpsPort The https port to accept traffic for this Repository on (optional)
@param v1Enabled Whether or not this Repository supports Docker V1 format
@param blobStoreName The BlobStore the Repository should use
@param members The names of the Repositories in the group
@return the newly created Repository

Repository createDockerGroup(final String name,
                             Integer httpPort,
                             Integer httpsPort,
                             final List<String> members,
                             final boolean v1Enabled,
                             final String blobStoreName);
*/

configuration = new Configuration(
        repositoryName: parsed_args.name,
        recipeName: 'docker-group',
        online: true,
        attributes: [
                docker : [
                        v1Enabled: Boolean.valueOf(parsed_args.v1_enabled)
                ],
                group  : [
                        memberNames: parsed_args.member_repos
                ],
                storage: [
                        blobStoreName: parsed_args.blob_store,
                        strictContentTypeValidation: Boolean.valueOf(parsed_args.strict_content_validation)
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
