# Vite 代理

api/intercepter.ts

```typescript
import axios from 'axios';
// 为所有请求添加前缀
axios.defaults.baseURL = '/api';
```



.env.development

```
VITE_API_BASE_URL='http://test.fuhualiang.club'
```



.env.production

```
VITE_API_BASE_URL='http://localhost'
```



vite.config.ts

```typescript
import { defineConfig, loadEnv } from "vite";
import path from "path";
import process from "process";

export default defineConfig(({ command, mode }) => {
  // 载入环境文件
  const env = loadEnv(mode, process.cwd());
  return {
    server: {
      proxy: {
        // 代理 URL 的前缀
	    '/api': {
          // 代理的目标地址
		  target: env.VITE_API_BASE_URL,
		  changeOrigin: true,
          // 必要时重写URL
          // rewrite: (path) => path.replace(env.VITE_API_BASE_PREFIX, ''),
		},
	  },
	},
  };
});

```

