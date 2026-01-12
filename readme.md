# Desafio Elevential

O desafio abaixo serve para avaliar os conhecimentos de frontend e backend do participante.

## Escopo
O seu cliente, Carvalho, é um catalogador de Pokemon. Ele precisa de um sistema para deixar mais fácil o cadastro de novas espécias que ele vem encontrando em suas pesquisas, de forma simples e rápida, através de um sistema web.
Carvalho já tem uma lista com 150 desses Pokemons, e o sistema deve vir com esses Pokemons pré-cadastrados.
[Esses dados podem ser encontrados aqui](./dados_iniciais.json)

Cada Pokemon terá as seguintes informações:
1. Código
2. Nome
3. Tipo
4. Tipo secundário
   1. Campo opcional

Cada tipo terá as seguintes informações:
1. Código
2. Nome


## Requisitos
1. Forma de cadastrar, listar, editar e deletar Pokemons, conforme a descrição do que precisa ser salvo em um Pokemon
2. Forma de cadastrar, listar, editar e deletar tipos
3. Criação dos Pokemons que Carvalho tem, dispnibilizaro [aqui](./dados_iniciais.json)
4. Listagem com filtros de nome e tipo
5. Repositório no github com o código
6. Descrição no readme do repositório, ensinando como rodar na máquina localmente o seu sistema

Caso não seja possível finalizar tudo, você pode enviar o que conseguiu finalizar!

## Opcionais
1. Video mostrando o uso do sistema no Youtube, no readme do repositório
2. Hospedagem na internet
3. Containerização do sistema

## Tecnologia Utilizada

O desafio pode ser realizado com qualquer tecnologia de preferência do participante.

Caso não tenha nenhuma em mente, recomendações possíveis são:
1. Python -> Django
2. Python -> Django + React
3. Node.js -> Mongo, Express, React("MERN")
4. C# -> DOTNET + React
5. Ruby -> Ruby on Rails

## Esboço do Banco de Dados
![img](Imagens/escopo_banco.png)

# Exemplo básico funcional
## [Um exemplo do que é esperado pode ser encontrado aqui](https://desafio-pokedex.elevential.com/)

# Meu Projeto
Sistema para catalogar pokemon (Pokedex) com backend Spring Boot (Java 17) e frontend Vue.js 3, utilizando MySQL 8.0 em container Docker.

## Pré-requisitos
### Backend
Java: OpenJDK 17.0.17 (Temurin)
Maven: 3.8.7
Spring Boot: 3.2.0

### Frontend
Node.js: 20.19.6
npm: 10.8.2
Vue.js: 3.5.26
Vite: 7.3.0

### Docker
Docker: 29.1.4
Docker Compose: 1.29.2
MySQL: 8.0

## Passo a passo para executar o projeto
### Clonar o projeto:
```git clone git@github.com:mjvini/desafio-elevential.git```

Depois é só ir para o diretório onde o projeto está.

### Suba o banco de dados com Docker
#### Execute o Docker Compose (raiz do projeto)
```docker-compose up -d```

#### Verifique se está rodando
```docker ps```

#### Deve mostrar: 
pokedex-mysql na porta 3307

#### Teste a conexão
```docker exec -it pokedex-mysql mysql -u mjvini -p```

Senha: Euamopokemon1
#### Comando MySQL: 
```SHOW DATABASES;```

Obs.: Deve aparecer o banco pokedexdb lá.

### Subir o backend Spring boot
#### Compile o projeto primeiro
```mvn clean compile```

#### Execute o projeto
```mvn spring-boot:run```

#### Acesse a API por:
http://localhost:8080/api

Observação: Para ver se a API subiu direitinho, abra o navegador e acesse http://localhost:8080/api/status

### Subir o frontend Vue.js
#### Instale as depedências 
```npm install```

#### Execute no modo dev
```npm run dev```

#### Testar a aplicação:
Acesse: http://localhost:5173


## Vídeo da aplicação rodando

#### link: https://youtu.be/gMAOQMotSRY
