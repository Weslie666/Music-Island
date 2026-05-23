import request from './request'

export function getStats() {
  return request.get('/admin/stats')
}

export function getUsers(page = 1, limit = 10) {
  return request.get('/admin/users', { params: { page, limit } })
}

export function updateUserStatus(id, status) {
  return request.put('/admin/users/' + id + '/status', { status })
}

export function getAdminComments(page = 1, limit = 10) {
  return request.get('/admin/comments', { params: { page, limit } })
}

export function deleteAdminComment(id) {
  return request.delete('/admin/comments/' + id)
}
