import request from './request'

export function getPopularRecommendations(limit = 10) {
  return request.get('/recommend/popular', { params: { limit } })
}

export function getPersonalRecommendations(limit = 10) {
  return request.get('/recommend/personal', { params: { limit } })
}

export function getSimilarSongs(songId, limit = 6) {
  return request.get('/recommend/similar/' + songId, { params: { limit } })
}
