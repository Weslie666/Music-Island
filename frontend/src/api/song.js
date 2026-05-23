import request from './request'

export function getSongs(params) {
  return request.get('/songs', { params })
}

export function getSongById(id) {
  return request.get('/songs/' + id)
}

export function getGenres() {
  return request.get('/songs/genres')
}

export function getStreamUrl(id) {
  return '/api/songs/' + id + '/stream'
}

export function addSong(data) {
  return request.post('/songs', data)
}

export function updateSong(id, data) {
  return request.put('/songs/' + id, data)
}

export function deleteSong(id) {
  return request.delete('/songs/' + id)
}
