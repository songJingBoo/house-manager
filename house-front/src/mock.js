export const Res = (data) => {
  return Promise.resolve({
    code: 200,
    success: true,
    data,
  })
}
export const filterConfig = [
  {
    name: '单价',
    code: 'unitPrice',
    suffix: '元/㎡',
    options: [{ max: 5000 }, { min: 5000, max: 8000 }, { min: 8000, max: 100000 }, { min: 10000 }],
    checked: [],
  },
  {
    name: '总价',
    code: 'totalPrice',
    suffix: '元/㎡',
    options: [{ max: 40 }, { min: 40, max: 60 }, { min: 60, max: 80 }, { min: 80 }],
    checked: [],
  },
  {
    name: '面积',
    code: 'area',
    suffix: '㎡',
    options: [{ max: 50 }, { min: 50, max: 70 }, { min: 70, max: 90 }, { min: 90 }],
    checked: [],
  },
  {
    name: '户型',
    code: 'layout',
    suffix: '居',
    options: [{ eq: 1 }, { eq: 2 }, { eq: 3 }, { eq: 4 }, { min: 5 }],
    checked: [],
  },
]
