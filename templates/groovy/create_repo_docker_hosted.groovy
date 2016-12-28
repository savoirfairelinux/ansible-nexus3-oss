import groovy.json.JsonSlurper
import org.sonatype.nexus.repository.config.Configuration

parsed_args = new JsonSlurper().parseText(args)

// https://javalibs.com/artifact/org.sonatype.nexus.plugins/nexus-script-plugin?className=org.sonatype.nexus.script.plugin.RepositoryApi&source
//   Repository createDockerHosted(final String name,                    // required
//                                 Integer httpPort,                     // optional
//                                 Integer httpsPort,                    // optional
//                                 String blobStoreName,                 // required
//                                 boolean v1Enabled,                    // required - docker specific
//                                 boolean strictContentTypeValidation,  // required
//                                 WritePolicy writePolicy);             // required

configuration = new Configuration(
        repositoryName: parsed_args.name,
        recipeName: 'docker-hosted',
        online: true,
        attributes: [
                docker  : [
                        v1Enabled: Boolean.valueOf(parsed_args.v1_enabled),
                ],
                storage: [
                        writePolicy: parsed_args.write_policy.toUpperCase(),
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
