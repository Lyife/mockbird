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
    {
      path: '/interfaces',
      component: () => import('../layout/MainLayout.vue'),
      children: [
        {
          path: '',
          component: () => import('../views/InterfaceList.vue'),
        },
      ],
    },
    {
      path: '/mock-rules',
      component: () => import('../layout/MainLayout.vue'),
      children: [
        {
          path: '',
          component: () => import('../views/MockRuleList.vue'),
        },
      ],
    },
    {
      path: '/online-test',
      component: () => import('../layout/MainLayout.vue'),
      children: [
        {
          path: '',
          component: () => import('../views/OnlineTest.vue'),
        },
      ],
    },
  ],
})

export default router
