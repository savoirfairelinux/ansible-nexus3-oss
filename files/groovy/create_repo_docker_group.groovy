import groovy.json.JsonSlurper
import org.sonatype.nexus.repository.config.Configuration

parsed_args = new JsonSlurper().parseText(args)

configuration = new Configuration(
        repositoryName: parsed_args.name,
        recipeName: 'docker-group',
        online: true,
        attributes: [
                docker: [
                        v1Enabled : parsed_args.v1_enabled
                ],
                group: [
                        memberNames: parsed_args.member_repos
                ],
                storage: [
                        writePolicy: parsed_args.write_policy.toUpperCase(),
                        blobStoreName: parsed_args.blob_store,
                        strictContentTypeValidation: Boolean.valueOf(parsed_args.strict_content_validation)
                ]
        ]
)

if (parsed_args.http_port) {
    configuration.attributes['docker']['httpPort'] = parsed_args.http_port
}

def existingRepository = repository.getRepositoryManager().get(parsed_args.name)

if (existingRepository != null) {
    existingRepository.stop()
    configuration.attributes['storage']['blobStoreName'] = existingRepository.configuration.attributes['storage']['blobStoreName']
    existingRepository.update(configuration)
    existingRepository.start()
} else {
    repository.getRepositoryManager().create(configuration)
}
