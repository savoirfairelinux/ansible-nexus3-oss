import groovy.json.JsonSlurper
import org.sonatype.nexus.blobstore.api.BlobStoreManager

parsed_args = new JsonSlurper().parseText(args)

existingBlobStore = blobStore.getBlobStoreManager().get(parsed_args.name)
if (existingBlobStore == null) {

    def config = blobStoreManager.newConfiguration()
    config.name = 'default'
    config.type = 'Google Cloud Storage'
    config.setAttributes(
      'google cloud storage': [
        bucket: parsed_args.bucket,
        credential_file: parsed_args.credentials
      ]
    )

    blobStoreManager.create(config)

}

