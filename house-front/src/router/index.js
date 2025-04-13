import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
    },
    {
      path: '/test',
      name: 'Test',
      component: () => import('@/views/detail/Appointment-dialog.vue'),
    },
    {
      path: '/',
      component: () => import('@/components/layout/front/front-layout.vue'),
      children: [
        {
          path: 'list',
          name: 'HouseList',
          component: () => import('@/views/List.vue'),
        },
        {
          path: 'publish',
          name: 'HousePublish',
          component: () => import('@/views/Publish.vue'),
        },
        {
          path: 'detail/:id',
          name: 'HouseDetail',
          component: () => import('@/views/Detail.vue'),
        },
        {
          path: '/',
          redirect: 'list',
        },
      ],
    },
    {
      path: '/back',
      component: () => import('@/components/layout/back/back-layout.vue'),
      children: [
        {
          path: 'audit',
          name: 'BackAudit',
          component: () => import('@/views-back/Audit.vue'),
        },
        {
          path: 'house',
          name: 'BackHouse',
          component: () => import('@/views-back/House.vue'),
        },
        {
          path: 'book',
          name: 'BackBook',
          component: () => import('@/views-back/Book.vue'),
        },
        {
          path: 'customer',
          name: 'BackCustomer',
          component: () => import('@/views-back/Customer.vue'),
        },
        {
          path: 'manager',
          name: 'BackManager',
          component: () => import('@/views-back/Manager.vue'),
        },
        {
          path: '/',
          redirect: 'audit',
        },
      ],
    },
  ],
})

export default router
