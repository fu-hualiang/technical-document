# Pinia 实现数据持久化

[新一代状态管理工具，Pinia.js 上手指南-技术圈 (proginn.com)](https://jishuin.proginn.com/p/763bfbd71cbf)

插件 pinia-plugin-persist 可以辅助实现数据持久化功能。

## 安装

```sh
npm i pinia-plugin-persist --save
```

## 使用

```js
// src/store/index.ts

import { createPinia } from 'pinia'
import piniaPluginPersist from 'pinia-plugin-persist'

const store = createPinia()
store.use(piniaPluginPersist)

export default store
```

接着在对应的 store 里开启 persist 即可。

```js
export const useUserStore = defineStore({
  id: 'user',

  state: () => {
    return {
      name: '张三'
    }
  },
  
  // 开启数据缓存
  persist: {
    enabled: true
  }
})
```

数据默认存在 sessionStorage 里，并且会以 store 的 id 作为 key。

## 自定义 key

你也可以在 strategies 里自定义 key 值，并将存放位置由 sessionStorage 改为 localStorage。

```js
persist: {
  enabled: true,
  strategies: [
    {
      key: 'my_user',
      storage: localStorage,
    }
  ]
}
```

## 持久化部分 state

默认所有 state 都会进行缓存，你可以通过 paths 指定要持久化的字段，其他的则不会进行持久化。

```js
state: () => {
  return {
    name: '张三',
    age: 18,
    gender: '男'
  }  
},

persist: {
  enabled: true,
  strategies: [
    {
      storage: localStorage,
      paths: ['name', 'age']
    }
  ]
}
```

上面我们只持久化 name 和 age，并将其改为localStorage, 而 gender 不会被持久化，如果其状态发送更改，页面刷新时将会丢失，重新回到初始状态，而 name 和 age 则不会。