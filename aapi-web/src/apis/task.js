import * as https from '@/utils/https'

export function start(id) {
    return https.get('/base/schedule-task/start/' + id)
} 

export function stop(id) {
    return https.get('/base/schedule-task/stop/' + id)
} 

export function restart(id) {
    return https.get('/base/schedule-task/restart/' + id)
} 

export function setInvalid(id) {
    return https.get('/base/schedule-task/invalid/' + id)
} 

export function setValid(id) {
    return https.get('/base/schedule-task/valid/' + id)
} 