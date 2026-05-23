import request from './request'

export function getComments(songId, page = 1, limit = 10) {
  return request.get('/comments', { params: { songId, page, limit } })
}

export function addComment(songId, content) {
  return request.post('/comments', { songId, content })
}

export function deleteComment(id) {
  return request.delete('/comments/' + id)
}
