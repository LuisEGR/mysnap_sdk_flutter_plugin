#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint my_snap_sdk.podspec' to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'my_snap_sdk'
  s.version          = '0.0.1'
  s.summary          = 'MySnapSDK Flutter Plugin'
  s.description      = <<-DESC
MySnapSDK Flutter Plugin
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }

  s.dependency 'Flutter'
  s.platform = :ios, '8.0'

  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.resource_bundles = { 'Classes' => 'Classes/**/*.storyboard'}
  s.vendored_frameworks = 'MiSnapSDK.framework', 'MiSnapSDKCamera.framework', 'MiSnapSDKMibiData.framework', 'MiSnapSDKScience.framework', 'MobileFlow.framework'
  s.frameworks = 'UIKit', 'Foundation', 'ImageIO','Security','QuartzCore','OpenGLES','MobileCoreServices', 'CoreVideo', 'CoreMedia', 'CoreGraphics', 'AVFoundation', 'AudioToolbox'
  s.resources = ['Classes/*.storyboard', 'MiSnapUX/UX2_Files/Resources/*', 'MiSnapUX/UX_Resources/*']
  s.info_plist = {
  'CFBundleIdentifier' => 'com.myorg.MyLib',
  'Privacy - Camera Usage Description' => 'Necesito acceder a tu camara'
}

  # Flutter.framework does not contain a i386 slice. Only x86_64 simulators are supported.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'VALID_ARCHS[sdk=iphonesimulator*]' => 'x86_64' }
end
