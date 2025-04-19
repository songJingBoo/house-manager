import { createRouter, createWebHistory } from 'vue-router'
import { useInfoStore } from '@/stores/userInfo'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
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
          path: 'user',
          name: 'UserCenter',
          component: () => import('@/views/UserCenter.vue'),
        },
        {
          path: '/',
          redirect: 'list',
        },
      ],
    },
    {
      path: '/back',
      name: 'Back',
      component: () => import('@/components/layout/back/back-layout.vue'),
      children: [
        {
          path: 'audit',
          name: 'BackAudit',
          meta: {
            role: ['Reviewer', 'Admin'],
          },
          component: () => import('@/views-back/Audit.vue'),
        },
        {
          path: 'house',
          name: 'BackHouse',
          meta: {
            role: ['Agent', 'Admin'],
          },
          component: () => import('@/views-back/House.vue'),
        },
        {
          path: 'book',
          name: 'BackBook',
          meta: {
            role: ['Agent', 'Admin'],
          },
          component: () => import('@/views-back/Book.vue'),
        },
        {
          path: 'customer',
          name: 'BackCustomer',
          meta: {
            role: ['Admin'],
          },
          component: () => import('@/views-back/Customer.vue'),
        },
        {
          path: 'manager',
          name: 'BackManager',
          meta: {
            role: ['Admin'],
          },
          component: () => import('@/views-back/Manager.vue'),
        },
      ],
    },
  ],
})

router.beforeEach(async (to, from, next) => {
  const state = useInfoStore()
  let { menuList, user } = state
  // if (!roleCode) {
  //   await store.dispatch('getUserInfo')
  //   roleCode = store.state.roleCode
  //   if (!roleCode) {
  //     next({ path: '/error' })
  //     return
  //   }
  // }

  // To根路径时，自动跳转第一个菜单
  if (to.path === '/back') {
    console.log(1)
    if (menuList.length) {
      next({ path: menuList[0].route })
    } else {
      next({ path: '/error' })
    }
    return
  }

  // // 正常情况下，不允许访问error页面
  // if (to.path === '/error') {
  //   if (roleCode && menuList.length) {
  //     next(from)
  //   } else {
  //     next()
  //   }
  //   return
  // }

  // 路由权限校验
  if (to.meta?.role?.length) {
    // 有限制
    if (to.meta.role.includes(user.role)) {
      next()
    } else {
      next(from)
    }
  } else {
    // 无限制
    next()
  }
})

export default router
