import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [

    {
      path: '/tipos',
      name: 'Tipomanager',
      component: () => import('@/components/Tipomanager.vue')
    }
  ],
})

export default router
