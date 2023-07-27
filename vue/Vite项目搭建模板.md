# Vite 项目搭建模板

## 初始化项目

### 搭建第一个 Vite 项目

> 兼容性注意
>
> Vite 需要 [Node.js](https://nodejs.org/en/) 版本 14.18+，16+。然而，有些模板需要依赖更高的 Node 版本才能正常运行，当你的包管理器发出警告时，请注意升级你的 Node 版本。

使用 NPM:

```bash
$ npm create vite@latest
```

使用 Yarn:

```bash
$ yarn create vite
```

使用 PNPM:

```bash
$ pnpm create vite
```

然后按照提示操作即可！

你还可以通过附加的命令行选项直接指定项目名称和你想要使用的模板。例如，要构建一个 Vite + Vue 项目，运行:

```bash
# npm 6.x
npm create vite@latest my-vue-app --template vue

# npm 7+, extra double-dash is needed:
npm create vite@latest my-vue-app -- --template vue

# yarn
yarn create vite my-vue-app --template vue

# pnpm
pnpm create vite my-vue-app --template vue
```

查看 [create-vite](https://github.com/vitejs/vite/tree/main/packages/create-vite) 以获取每个模板的更多细节：`vanilla`，`vanilla-ts`, `vue`, `vue-ts`，`react`，`react-ts`，`react-swc`，`react-swc-ts`，`preact`，`preact-ts`，`lit`，`lit-ts`，`svelte`，`svelte-ts`。



### *修改 tsconfig.json

```json

```



### *修改 vite.config.ts

```typescript

```



## 代码质量风格的统一

### 配置 ESLint

安装

```bash
pnpm add eslint -D
```

初始化

```shell
npm init @eslint/config
```

修改 .eslintrc.*

```js
// 刚创建完成时会出现'module' is not defined.文件修改后报错会消失，或者尝试在env下添加node: true
```

创建.eslintignore文件



### 配置 Prettier

安装

```bash
pnpm add prettier -D
```

创建配置文件

```bash
echo {}> .prettierrc.json
```

修改.prettierrc.json

```json
{
    "printWidth":80, // 默认
    "tabWidth":2, // 默认
    "semi": true, // 默认
    "singleQuote": true,
    "quoteProps": "consistent",
    "htmlWhitespaceSensitivity":"ignore",
    "vueIndentScriptAndStyle": true
}
```

创建.prettierignore文件



### 集成 ESLint 和 Prettier

Linters usually contain not only code quality rules, but also stylistic rules. Most stylistic rules are unnecessary when using Prettier, but worse – they might conflict with Prettier! Use Prettier for code formatting concerns, and linters for code-quality concerns, as outlined in [Prettier vs. Linters](https://prettier.io/docs/en/comparison.html).

Luckily it’s easy to turn off rules that conflict or are unnecessary with Prettier, by using these pre-made configs:

- [eslint-config-prettier](https://github.com/prettier/eslint-config-prettier)
- [stylelint-config-prettier](https://github.com/prettier/stylelint-config-prettier)

Check out the above links for instructions on how to install and set things up.



安装 eslint-config-prettier

```bash
pnpm add eslint-config-prettier -D
```

然后，添加"prettier"到文件.eslintrc.*中的“extends”数组中。确保将其放在最后，覆盖其他配置。

```js
{
  "extends": [
    "some-other-config-you-use",
    "prettier"
  ]
}
```

安装 eslint-plugin-prettier（可选）

```bash
pnpm add eslint-plugin-prettier -D
```

修改 .eslintrc.json（可选）

```json
{
  "extends": [
    "some-other-config-you-use",
    "plugin:prettier/recommended"
  ]
}
```





## 组件集成

### 集成 Pinia



### 集成 Vue Router



### 集成 VueUse



### 集成 Axios

