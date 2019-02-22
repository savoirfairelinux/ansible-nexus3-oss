import groovy.json.JsonSlurper
import org.sonatype.nexus.blobstore.api.BlobStoreConfiguration
import org.sonatype.nexus.blobstore.api.BlobStoreManager

parsed_args = new JsonSlurper().parseText(args)

existingBlobStore = blobStore.getBlobStoreManager().get(parsed_args.name)
if (existingBlobStore == null) {

    def blobStoreManager = container.lookup(BlobStoreManager.class.name)
    blobStoreManager.create(new BlobStoreConfiguration(name: parsed_args.name, type: 'Google Cloud Storage',
    attributes: [
        'google cloud storage': [
            bucket: parsed_args.bucket,
            credential_file: parsed_args.credentials
        ]
    ]))
}

