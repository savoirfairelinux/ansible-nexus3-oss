import groovy.json.JsonSlurper

parsed_args = new JsonSlurper().parseText(args)

if (!parsed_args) {
    core.removeHTTPProxy()
} else {
    if (parsed_args.auth) {
        core.httpProxyWithBasicAuth(parsed_args.host, parsed_args.port as int, parsed_args.username, parsed_args.password)
    } else {
        core.httpProxy(parsed_args.host, parsed_args.port as int)
    }

    if (parsed_args.https_enabled) {
        if (parsed_args.https_auth) {
            core.httpsProxyWithBasicAuth(parsed_args.host, parsed_args.port as int, parsed_args.username, parsed_args.password)
        } else {
            core.httpsProxy(parsed_args.host, parsed_args.port as int)
        }
    } else {
        core.removeHTTPSProxy()
    }

    core.nonProxyHosts(parsed_args.non_proxy_hosts as String[])
}
