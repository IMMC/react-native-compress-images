/**
** author: cjp
** time: 2018/1/15
**/
import {NativeModules} from 'react-native';
import PropTypes from 'prop-types';
const optionsTypes = {
    maxWidth: PropTypes.number,
    maxHeight: PropTypes.number,
    quality: PropTypes.number,
    urlList: PropTypes.array,
    saveImages: PropTypes.bool,
    resultBase64: PropTypes.bool
};
const secondOptions = {
    urlList: PropTypes.array
};
// const CompressImage = NativeModules.CompressImage;
export default function CompressImages(options) {
    if (options.type === 'quality') {
        PropTypes.checkPropTypes(secondOptions, options, 'props', 'react-native-compress-images');
        return NativeModules.CompressImages.compressQuality(options);
    } else {
        PropTypes.checkPropTypes(optionsTypes, options, 'prop', 'react-native-compress-images');
        return NativeModules.CompressImages.compressSize(options);
    }

};