/*------ Initialize thymol.js ------*/

webJars = function (basePath) {
  return {
    jQuery: basePath + 'jquery/3.0.0-alpha1/jquery.min.js',
    thymolFull: basePath + 'thymol.js/2.0.0/thymol-full.min.js',
    lessJs: basePath + 'less/2.5.3/less.min.js',
  }
}('../../../../target/webjars/META-INF/resources/webjars/')

thymol = {
  thDomParser: DOMParser,
  thWindow: window,

  // Defaults that must be defined.
  thDefaultPrefix: 'th',
  thDefaultDataPrefix: 'data',
  thDefaultPrecision: 10,
  thDefaultProtocol: 'file:///',
  thDefaultLocale: 'en',
  thDefaultPrecedence: 2e4,
  thDefaultMessagePath: '../config/web/i18n',
  thDefaultResourcePath: '',
  thDefaultMessagesBaseName: 'web-messages',
  thDefaultRelativeRootPath: '',
  thDefaultExtendedMapping: true,
  thDefaultLocalMessages: true,
  thDefaultDisableMessages: false,
  thDefaultTemplateSuffix: '.html',
}

thymolDeferredFunctions = [function () {
  thymol.configurePostExecution(function () {
    $('head').append('<link type="text/css" rel="stylesheet/less" href="../../less/style.local.less">')
    $.getScript(webJars.lessJs)
  })
}]

thMappings = [
  ['/images', '../static/images']
]

;(function () {
  var loadScript = function (script) {
    var el = document.createElement('script')
    el.async = false
    el.src = script
    el.type = 'text/javascript'
    ;(document.getElementsByTagName('head')[0] || document.body).appendChild(el)
  }

  loadScript(webJars.jQuery)  // required by thymol
  loadScript(webJars.thymolFull)
})()
