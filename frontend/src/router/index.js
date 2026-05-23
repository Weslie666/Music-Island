import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/discover',
    name: 'Discover',
    component: () => import('../views/Discover.vue')
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('../views/Search.vue')
  },
  {
    path: '/song/:id',
    name: 'SongDetail',
    component: () => import('../views/SongDetail.vue')
  },
  {
    path: '/playlist/:id',
    name: 'PlaylistDetail',
    component: () => import('../views/PlaylistDetail.vue')
  },
  {
    path: '/my/favorites',
    name: 'MyFavorites',
    component: () => import('../views/MyFavorites.vue')
  },
  {
    path: '/my/history',
    name: 'MyHistory',
    component: () => import('../views/MyHistory.vue')
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('../views/admin/Dashboard.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
