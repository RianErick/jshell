# JShell - Shell Interativo em Java

Um shell interativo desenvolvido em Java que oferece funcionalidades similares ao Bash, incluindo comandos builtin, histórico de comandos e listagem de arquivos com cores.

---

## Sumário

- [Sobre o Projeto](#sobre-o-projeto)
- [Requisitos](#requisitos)
- [Instalação](#instalação)
- [Uso](#uso)
- [Comandos Builtin](#comandos-builtin)
- [Recursos](#recursos)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Contribuição](#contribuição)
- [Licença](#licença)

---

## Sobre o Projeto

O RShell é um interpretador de comandos (shell) escrito em Java que permite executar comandos do sistema operacional de forma interativa. Foi desenvolvido como um projeto educacional para demonstrar conceitos de sistemas operacionais, manipulação de processos e I/O em Java.

### Principais Características

- Execução de comandos externos do sistema operacional
- Comandos builtin implementados nativamente (cd, ls, pwd, etc.)
- Histórico de comandos persistente
- Listagem de arquivos colorida por tipo
- Prompt dinâmico com diretório atual
- Suporte a caminhos absolutos e relativos

---

## Requisitos

| Requisito | Versão Mínima |
|-----------|---------------|
| Java JDK  | 11+           |
| Sistema   | Linux/macOS   |

> **Nota:** O projeto utiliza APIs POSIX para permissões de arquivos, portanto funciona melhor em sistemas Unix-like.

---

## Instalação

### Compilação Manual

```bash
# Clone o repositório
git clone <url-do-repositorio>
cd shell

# Compile os arquivos
javac -d target/classes src/main/java/enums/Colors.java \
      src/main/java/util/InputOutputSystem.java \
      src/main/java/Main.java
```

### Usando Maven (se disponível)

```bash
mvn compile
```

---

## Uso

### Iniciar o Shell

```bash
java -cp target/classes Main
```

### Exemplo de Sessão

```
~/Documents/projetos rshell> ls
src/       target/    pom.xml    README.MD

~/Documents/projetos rshell> cd src

~/Documents/projetos/src rshell> ls -la
total 4
drwxr-xr-x  1 usuario  usuario      4096 Feb 08 14:30 main/

~/Documents/projetos/src rshell> pwd
/home/usuario/Documents/projetos/src

~/Documents/projetos/src rshell> exit
Saindo do rshell... Até mais!
```

---

## Comandos Builtin

O RShell implementa os seguintes comandos nativamente:

| Comando       | Descrição                                      | Exemplo            |
|---------------|------------------------------------------------|--------------------|
| `cd [dir]`    | Altera o diretório de trabalho atual           | `cd /home/usuario` |
| `cd ~`        | Vai para o diretório home do usuário           | `cd ~`             |
| `cd ..`       | Volta para o diretório pai                     | `cd ..`            |
| `pwd`         | Exibe o caminho absoluto do diretório atual    | `pwd`              |
| `ls`          | Lista arquivos em formato de colunas           | `ls`               |
| `ls -a`       | Lista arquivos incluindo ocultos               | `ls -a`            |
| `ls -l`       | Lista arquivos em formato longo                | `ls -l`            |
| `ls -la`      | Combina listagem longa com arquivos ocultos    | `ls -la`           |
| `history`     | Exibe os últimos comandos executados           | `history`          |
| `clear`       | Limpa a tela do terminal                       | `clear`            |
| `help`        | Exibe a ajuda dos comandos disponíveis         | `help`             |
| `exit`        | Encerra o shell                                | `exit`             |

### Comandos Externos

Qualquer comando não listado acima será executado como comando externo do sistema operacional:

```
rshell> cat arquivo.txt
rshell> grep "texto" arquivo.txt
rshell> whoami
rshell> date
```

---

## Recursos

### Listagem de Arquivos Colorida

O comando `ls` exibe arquivos com cores baseadas no tipo:

| Tipo de Arquivo       | Cor      | Indicador |
|-----------------------|----------|-----------|
| Diretórios            | Azul     | `/`       |
| Executáveis           | Verde    | `*`       |
| Links simbólicos      | Ciano    | -         |
| Arquivos compactados  | Vermelho | -         |
| Imagens               | Roxo     | -         |
| Código fonte          | Amarelo  | -         |
| Outros arquivos       | Branco   | -         |

### Formato Longo (ls -l)

```
drwxr-xr-x   1 usuario  usuario      4096 Feb 08 14:30 src/
-rw-r--r--   1 usuario  usuario      1234 Feb 08 14:25 pom.xml
-rwxr-xr-x   1 usuario  usuario      8192 Feb 08 14:20 script.sh*
```

Campos exibidos:
1. Permissões (tipo + rwx para owner/group/others)
2. Número de links
3. Proprietário
4. Grupo
5. Tamanho em bytes
6. Data de modificação
7. Nome do arquivo

### Histórico de Comandos

- Os comandos são salvos automaticamente em `hist.txt`
- O histórico mantém os últimos 20 comandos
- Use `history` para visualizar os 5 comandos mais recentes

---

## Limitações

- Não suporta pipes (`|`) ou redirecionamento (`>`, `<`)
- Não suporta execução em background (`&`)
- Não suporta variáveis de ambiente customizadas
- Não suporta wildcards (`*`, `?`)
- Não suporta auto-complete (Tab)

---

## Contribuição

Contribuições são bem-vindas. Para contribuir:

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas alterações (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

### Sugestões de Melhorias

- Implementar suporte a pipes
- Adicionar redirecionamento de entrada/saída
- Implementar auto-complete com Tab
- Adicionar suporte a aliases
- Implementar job control (Ctrl+Z, bg, fg)

---

## Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

---

## Autor

Desenvolvido como projeto educacional para estudo de sistemas operacionais e programação em Java.
