#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
require 'json'
pjson = JSON.parse(File.read('package.json'))

Pod::Spec.new do |s|

  s.name            = "openinstall-react-native"
  s.version         = pjson["version"]
  s.homepage        = "https://github.com/OpenInstall/openinstall-react-native"
  s.license         = pjson["license"]
  s.summary         = pjson["description"]
  s.author          = pjson["author"]

  s.ios.deployment_target = '8.0'

  s.source          = { :git => "https://github.com/OpenInstall/openinstall-react-native.git" }
  s.source_files    = 'ios/RCTOpenInstall/RCTOpenInstall/*.{h,m}'
  s.preserve_paths  = "*.js"
  s.frameworks      = 'UIKit','Foundation'
  s.vendored_libraries = "ios/RCTOpenInstall/RCTOpenInstall/*.a"

  s.dependency 'React'

end