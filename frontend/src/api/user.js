import request from './request'

// Likes
export function getMyLikes(page = 1, limit = 10) {
  return request.get('/likes', { params: { page, limit } })
}

export function toggleLike(songId) {
  return request.post('/likes/' + songId)
}

export function checkLiked(songId) {
  return request.get('/likes/' + songId)
}

// Play History
export function getMyHistory(page = 1, limit = 20) {
  return request.get('/history', { params: { page, limit } })
}

export function recordPlay(songId) {
  return request.post('/history/' + songId)
}

// Playlists
export function getMyPlaylists() {
  return request.get('/playlists')
}

export function getPlaylistDetail(id) {
  return request.get('/playlists/' + id)
}

export function createPlaylist(data) {
  return request.post('/playlists', data)
}

export function updatePlaylist(id, data) {
  return request.put('/playlists/' + id, data)
}

export function deletePlaylist(id) {
  return request.delete('/playlists/' + id)
}

export function addToPlaylist(playlistId, songId) {
  return request.post('/playlists/' + playlistId + '/songs', { songId })
}

export function removeFromPlaylist(playlistId, songId) {
  return request.delete('/playlists/' + playlistId + '/songs/' + songId)
}
