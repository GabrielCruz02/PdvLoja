# PDV Lojas ![image](https://github.com/user-attachments/assets/3abeee42-95cc-4bfb-b47d-6fd9cb8259d6)


**Status do Projeto:** Concluído ✔️

## 📝 Descrição

Um sistema de Ponto de Venda (PDV) desktop simples, desenvolvido em Java com JavaFX. O projeto foi criado como um exercício prático para cobrir o ciclo completo de desenvolvimento de software, desde a concepção e modelagem até a criação de um instalador distribuível.

---

## 📸 Demonstração


---

## ✨ Funcionalidades

* **Gestão de Caixa:** Abertura de caixa com valor inicial e fechamento com relatório detalhado.
* **Cadastro de Produtos:** Interface CRUD (Criar, Ler, Atualizar, Deletar) completa para gerenciar os produtos da loja.
* **Registro de Vendas:** Lançamento de itens na venda, cálculo de total e troco em tempo real.
* **Formas de Pagamento:** Suporte para vendas em Dinheiro, Cartão de Crédito e Pix.
* **Relatório Diário:** Geração de um resumo com os totais do dia ao fechar o caixa.
* **Persistência de Dados:** Todas as informações são salvas localmente em um banco de dados SQLite.

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 17
* **Interface Gráfica:** JavaFX
* **Banco de Dados:** SQLite
* **Gerenciador de Dependências:** Maven
* **Empacotamento:** jpackage (para criar o instalador `.msi`)

---

## 🚀 Como Executar

Existem duas formas de executar o projeto:

### 1. Para Usuários Finais (Instalador)

1.  Vá para a seção **[Releases](https://github.com/GabrielCruz02/PdvLoja/releases/tag/PDV)** deste repositório.
2.  Baixe o arquivo `.msi` da versão mais recente.
3.  Execute o instalador e siga as instruções. O programa será instalado e um atalho será criado na sua Área de Trabalho/Menu Iniciar.

### 2. Para Desenvolvedores (Ambiente de Desenvolvimento)

**Pré-requisitos:**
* JDK 17 ou superior
* Maven 3.8+
* Uma IDE de sua preferência (ex: IntelliJ IDEA)

**Passos:**
```bash
# Clone o repositório
git clone [COLOQUE AQUI O LINK DO SEU REPOSITÓRIO GIT]

# Navegue até a pasta do projeto
cd PdvLoja

# O projeto será aberto e as dependências serão baixadas pelo Maven.
# Você pode executar a aplicação através da classe MainApp.java na sua IDE.

---

🏛️ Estrutura do Projeto
O projeto segue uma arquitetura baseada no padrão MVC, com a seguinte divisão de pacotes:

app: Contém a classe principal que inicia a aplicação JavaFX.
controller: Contém as classes controladoras para cada tela .fxml, gerenciando a interação do usuário.
dao: (Data Access Object) Contém as classes responsáveis pela comunicação direta com o banco de dados (operações SQL).
model: Contém as classes de entidade (POJOs) que representam os dados do sistema (Produto, Venda, etc.).
util: Contém classes utilitárias, como a ConnectionFactory e o DatabaseInitializer.

```

---

👨‍💻 Autor
Gabriel Alves Silva Cruz

[LinkedIn](https://www.linkedin.com/in/gabriel-cruz-4a950a275/) | [GitHub](https://github.com/GabrielCruz02)


---
