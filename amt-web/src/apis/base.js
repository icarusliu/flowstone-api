import https from '../utils/https.js';

export default {
    getApps() {
        return https.post('/base/app/query')
    }
}