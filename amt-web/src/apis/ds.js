import * as https from '@/utils/https'

export function test(ds) {
    return https.post('/base/ds/test', ds)
}