import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('../views/Home.vue'),
    },
    {
      path: '/projects',
      component: () => import('../layout/MainLayout.vue'),
      children: [
        {
          path: '',
          component: () => import('../views/ProjectList.vue'),
        },
      ],
    },
  ],
})

export default router
