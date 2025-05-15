# Batata Quente - Plugin para Minecraft

Plugin de Batata Quente desenvolvido para servidores Minecraft, com sistema completo de mensagens customizáveis e logs.

> **ATENÇÃO:** O plugin deve ser executado em Java 8!

## Funcionalidades

- Sistema de mensagens customizáveis via arquivos YAML
- Suporte a múltiplos idiomas (português e inglês)
- Sistema avançado de logs com limpeza automática
- Comandos de desenvolvimento e debug para administradores
- Estrutura organizada e código limpo

## Comandos

### Comando Principal: `/dev`

- `/dev on` - Ativa o modo debug
- `/dev off` - Desativa o modo debug
- `/dev add [jogador]` - Adiciona um jogador à lista de debug
- `/dev remove [jogador]` - Remove um jogador da lista de debug
- `/dev reload` - Recarrega configurações e mensagens
- `/dev logs list` - Lista todos os arquivos de log disponíveis
- `/dev logs view <data>` - Visualiza um arquivo de log específico (formato: yyyy-MM-dd)
- `/dev test` - Envia mensagens de teste para o console

## Configuração

As configurações estão disponíveis no arquivo `config.yml`:

```yaml
# Configurações gerais
language: ptbr        # Idioma (ptbr ou enus)
prefix-enabled: true  # Mostrar prefixo nas mensagens

# Debug
debug: false          # Modo debug ativado/desativado

# Logs
logging:
  enabled: true       # Sistema de logs ativado/desativado
  debug-to-file: true # Salvar mensagens de debug em arquivo
  max-log-days: 7     # Dias para manter logs antigos
```

## Idiomas

O plugin suporta múltiplos idiomas através de arquivos de mensagens:

- `ptbr` - Português Brasileiro (padrão)
- `enus` - Inglês Americano

Os arquivos de mensagens são criados automaticamente na pasta do plugin.

## Permissões

- `onyell.dev` - Acesso a todos os comandos de desenvolvedor
- `onyell.admin` - Acesso a todos os comandos administrativos (inclui onyell.dev)

## Desenvolvimento

Para desenvolvedores que desejam contribuir ou modificar o plugin:

- O código está estruturado em pacotes organizados por funcionalidade
- Sistema de mensagens baseado em enums para fácil manutenção
- Logs são salvos em arquivos diários no formato `log-yyyy-MM-dd.log`
- Sistema de limpeza automática de logs antigos

---