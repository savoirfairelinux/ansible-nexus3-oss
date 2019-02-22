import groovy.json.JsonSlurper
import org.sonatype.nexus.blobstore.api.BlobStoreConfiguration
import org.sonatype.nexus.blobstore.api.BlobStoreManager

parsed_args = new JsonSlurper().parseText(args)

existingBlobstore = BlobStoreManager.get(parsed_args.name)

if ( existingBlobstore != null) {
    newConfig = existingBlobstore.configuration.copy()
    newConfig.attributes['google cloud storage']['credentials'] = parsed_args.credentials

    blobStoreManager.update(newConfig)
} else {
    def blobStoreManager = container.lookup(BlobStoreManager.class.name)
    blobStoreManager.create(new BlobStoreConfiguration(name: parsed_args.name, type: 'Google Cloud Storage',
    attributes: [
        'google cloud storage': [
            bucket: parsed_args.bucket,
            credentials: parsed_args.credentials
        ]
    ]))
}

