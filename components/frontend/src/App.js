import { Layout } from "antd";
// import logo from './logo.svg';
import './App.css';
import { AntTable } from "./Table/AntTable";

function App() {
  const { Header, Content, Footer } = Layout;

  return (
    <Layout>
      <Header className="header">Header</Header>
      <Content className="content">
        <AntTable />
      </Content>
      <Footer className="footer">Footer</Footer>
    </Layout>
  );
}

export default App;
