var exec = require('cordova/exec');

exports.getVersion = function (arg0, success, error) {
  exec(success, error, 'CLiteAV', 'getVersion', [arg0]);
};

exports.startPlay = function(url, playType, success, error) {
  cordova.exec(success, error, 'CLiteAV', 'startPlay', [url, playType]);
};
