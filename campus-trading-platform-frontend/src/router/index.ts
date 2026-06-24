import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layout/MainLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/login/LoginView.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/login/RegisterView.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/home/HomeView.vue'),
        },
        {
          path: 'publish',
          name: 'publish',
          component: () => import('@/views/publish/PublishView.vue'),
        },
        {
          path: 'item/:id',
          name: 'itemDetail',
          component: () => import('@/views/item/ItemDetailView.vue'),
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('@/views/profile/ProfileView.vue'),
        },
        {
          path: 'user/:id',
          name: 'userProfile',
          component: () => import('@/views/user/UserProfileView.vue'),
        },
        {
          path: 'review/submit',
          name: 'submitReview',
          component: () => import('@/views/review/SubmitReviewView.vue'),
          props: route => ({
            itemId: route.query.itemId,
            revieweeId: route.query.revieweeId
          }),
        },
      ],
    },
  ],
})

// 路由守卫 - 确保返回值
router.beforeEach((to) => {
  const token = localStorage.getItem('token')

  // 需要认证但没有 token，跳转到登录页
  if (to.meta.requiresAuth && !token) {
    return { name: 'login' }
  }

  // 已登录但访问登录/注册页，跳转到首页
  if ((to.name === 'login' || to.name === 'register') && token) {
    return { name: 'home' }
  }

  // 其他情况正常放行
  return true
})

export default router