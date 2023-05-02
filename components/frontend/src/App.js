import { useCallback } from "react";
import { Layout } from "antd";
import { AntTable } from "./Table/AntTable";
import './App.css';
import { getForTable } from "./rest/crud";
import { crudColumns } from "./columns/columns";

function App() {
  const restData = useCallback(payload => getForTable(payload), []);
  const getColumns = useCallback(() => crudColumns(), []);

  const { Header, Content, Footer } = Layout;

  return (
    <Layout>
      <Header className="header">Header</Header>
      <Content className="content">
        <AntTable {...{ restData, getColumns }} />
      </Content>
      <Footer className="footer">Footer</Footer>
    </Layout>
  );
}

export default App;
